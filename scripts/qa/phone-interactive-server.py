#!/usr/bin/env python3
"""Interactive ADB phone mirror — click/swipe in Cursor Simple Browser."""

from __future__ import annotations

import argparse
import json
import os
import struct
import subprocess
import sys
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

HTML_PATH = Path(__file__).with_name("phone-interactive-remote.html")
PACKAGE = "com.widgetg7.mobile/.SplashActivity"

KEY_BACK = 4
KEY_HOME = 3
KEY_RECENTS = 187


class PhoneController:
    def __init__(self, adb: str, serial: str) -> None:
        self.adb = adb
        self.serial = serial
        self.lock = threading.Lock()
        self.screen_w = 1080
        self.screen_h = 2400

    def _run(self, *args: str, timeout: int = 20) -> subprocess.CompletedProcess[bytes]:
        return subprocess.run(
            [self.adb, "-s", self.serial, *args],
            capture_output=True,
            timeout=timeout,
            check=False,
        )

    @staticmethod
    def _png_size(data: bytes) -> tuple[int, int] | None:
        if len(data) >= 24 and data[:8] == b"\x89PNG\r\n\x1a\n":
            w, h = struct.unpack(">II", data[16:24])
            return w, h
        return None

    def capture(self) -> bytes:
        with self.lock:
            proc = self._run("exec-out", "screencap", "-p")
            if proc.returncode != 0 or len(proc.stdout) < 1000:
                raise RuntimeError(
                    proc.stderr.decode("utf-8", errors="replace") or "screencap failed"
                )
            size = self._png_size(proc.stdout)
            if size:
                self.screen_w, self.screen_h = size
            return proc.stdout

    def tap(self, x: int, y: int) -> None:
        self._run("shell", "input", "tap", str(x), str(y))

    def swipe(self, x1: int, y1: int, x2: int, y2: int, duration_ms: int = 250) -> None:
        self._run(
            "shell",
            "input",
            "swipe",
            str(x1),
            str(y1),
            str(x2),
            str(y2),
            str(duration_ms),
        )

    def key(self, keycode: int) -> None:
        self._run("shell", "input", "keyevent", str(keycode))

    def launch_app(self) -> None:
        self._run("shell", "am", "start", "-n", PACKAGE)

    def info(self) -> dict[str, Any]:
        return {
            "serial": self.serial,
            "width": self.screen_w,
            "height": self.screen_h,
            "package": PACKAGE,
        }


def make_handler(phone: PhoneController, html: bytes):
    class Handler(BaseHTTPRequestHandler):
        def log_message(self, fmt: str, *args: Any) -> None:
            if self.path.startswith("/api/screen"):
                return
            sys.stderr.write("%s - %s\n" % (self.address_string(), fmt % args))

        def _json(self, code: int, payload: dict[str, Any]) -> None:
            body = json.dumps(payload).encode("utf-8")
            self.send_response(code)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def _read_json(self) -> dict[str, Any]:
            length = int(self.headers.get("Content-Length", "0"))
            raw = self.rfile.read(length) if length else b"{}"
            return json.loads(raw.decode("utf-8"))

        def do_GET(self) -> None:
            path = urlparse(self.path).path
            if path in ("/", "/index.html"):
                self.send_response(200)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.send_header("Content-Length", str(len(html)))
                self.end_headers()
                self.wfile.write(html)
                return
            if path == "/api/info":
                self._json(200, phone.info())
                return
            if path == "/api/screen":
                try:
                    png = phone.capture()
                except Exception as exc:  # noqa: BLE001
                    self._json(500, {"error": str(exc)})
                    return
                self.send_response(200)
                self.send_header("Content-Type", "image/png")
                self.send_header("Cache-Control", "no-store")
                self.send_header("Content-Length", str(len(png)))
                self.end_headers()
                self.wfile.write(png)
                return
            self.send_error(404)

        def do_POST(self) -> None:
            path = urlparse(self.path).path
            try:
                body = self._read_json()
            except json.JSONDecodeError:
                self._json(400, {"error": "invalid json"})
                return

            try:
                if path == "/api/tap":
                    phone.tap(int(body["x"]), int(body["y"]))
                    self._json(200, {"ok": True})
                    return
                if path == "/api/swipe":
                    phone.swipe(
                        int(body["x1"]),
                        int(body["y1"]),
                        int(body["x2"]),
                        int(body["y2"]),
                        int(body.get("durationMs", 250)),
                    )
                    self._json(200, {"ok": True})
                    return
                if path == "/api/key":
                    key = body.get("key", "")
                    mapping = {"BACK": KEY_BACK, "HOME": KEY_HOME, "RECENTS": KEY_RECENTS}
                    if key not in mapping:
                        self._json(400, {"error": "unknown key"})
                        return
                    phone.key(mapping[key])
                    self._json(200, {"ok": True})
                    return
                if path == "/api/launch":
                    phone.launch_app()
                    self._json(200, {"ok": True})
                    return
            except Exception as exc:  # noqa: BLE001
                self._json(500, {"error": str(exc)})
                return

            self.send_error(404)

    return Handler


def main() -> int:
    parser = argparse.ArgumentParser(description="Interactive phone mirror for Cursor")
    parser.add_argument("--adb", default=os.environ.get("WIDGETG7_ADB", "adb"))
    parser.add_argument("--serial", default=os.environ.get("WIDGETG7_PHONE_SERIAL", ""))
    parser.add_argument("--port", type=int, default=8766)
    args = parser.parse_args()

    if not args.serial:
        print("Missing --serial or WIDGETG7_PHONE_SERIAL", file=sys.stderr)
        return 1

    if not HTML_PATH.is_file():
        print(f"Missing HTML: {HTML_PATH}", file=sys.stderr)
        return 2

    html = HTML_PATH.read_bytes()
    phone = PhoneController(args.adb, args.serial)
    handler = make_handler(phone, html)
    server = ThreadingHTTPServer(("127.0.0.1", args.port), handler)
    print(f"Phone interactive mirror on http://127.0.0.1:{args.port}/  serial={args.serial}")
    print("Cursor: Ctrl+Shift+P -> Simple Browser: Show -> paste URL above")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
