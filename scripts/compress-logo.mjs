import sharp from "sharp";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.join(__dirname, "..");
const outDir = path.join(root, "mobile", "src", "main", "res", "drawable-nodpi");

const source =
  process.argv[2] ?? path.join(__dirname, "official-logo.jpg");

if (!fs.existsSync(source)) {
  console.error(`Source not found: ${source}`);
  process.exit(1);
}

fs.mkdirSync(outDir, { recursive: true });

async function exportLogo(baseName) {
  const output = path.join(outDir, `${baseName}.webp`);
  const meta = await sharp(source).metadata();
  const width = meta.width ?? 0;
  const height = meta.height ?? 0;

  const trimmed = await sharp(source)
    .trim({ threshold: 12 })
    .toBuffer();
  const trimmedMeta = await sharp(trimmed).metadata();
  const tw = trimmedMeta.width ?? width;
  const th = trimmedMeta.height ?? height;
  const cropSize = Math.min(tw, th);
  const left = Math.floor((tw - cropSize) / 2);
  const top = Math.floor((th - cropSize) / 2);

  await sharp(trimmed)
    .extract({ left, top, width: cropSize, height: cropSize })
    .resize(512, 512, { fit: "fill" })
    .webp({ quality: 95, effort: 6, smartSubsample: true })
    .toFile(output);

  const bytes = fs.statSync(output).size;
  console.log(
    `${baseName}.webp\t${bytes} bytes\t(from ${width}x${height}, trim ${tw}x${th}, crop ${cropSize}x${cropSize})`,
  );
}

await exportLogo("ic_app_logo");
await exportLogo("ic_launcher_foreground");
