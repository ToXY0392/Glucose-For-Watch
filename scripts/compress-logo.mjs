import sharp from "sharp";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.join(__dirname, "..");
const resRoot = path.join(root, "mobile", "src", "main", "res");

const source =
  process.argv[2] ?? path.join(__dirname, "official-logo.jpg");

if (!fs.existsSync(source)) {
  console.error(`Source not found: ${source}`);
  process.exit(1);
}

const EXPORT_SIZE = 512;

async function buildSquareLogoBuffer(size) {
  const trimmed = await sharp(source).trim({ threshold: 12 }).toBuffer();
  const trimmedMeta = await sharp(trimmed).metadata();
  const tw = trimmedMeta.width ?? 0;
  const th = trimmedMeta.height ?? 0;
  const cropSize = Math.min(tw, th);
  const left = Math.floor((tw - cropSize) / 2);
  const top = Math.floor((th - cropSize) / 2);

  return sharp(trimmed)
    .extract({ left, top, width: cropSize, height: cropSize })
    .resize(size, size, { fit: "fill", kernel: sharp.kernel.lanczos3 })
    .webp({ quality: 95, effort: 6, smartSubsample: true })
    .toBuffer();
}

async function writeLogo(relativeDir, baseName, buffer) {
  const outDir = path.join(resRoot, relativeDir);
  fs.mkdirSync(outDir, { recursive: true });
  const output = path.join(outDir, `${baseName}.webp`);
  await fs.promises.writeFile(output, buffer);
  console.log(`${relativeDir}/${baseName}.webp\t${buffer.length} bytes`);
}

const legacyBuckets = [
  "drawable-mdpi",
  "drawable-hdpi",
  "drawable-xhdpi",
  "drawable-xxhdpi",
  "drawable-xxxhdpi",
];
for (const folder of legacyBuckets) {
  const legacy = path.join(resRoot, folder, "ic_app_logo.webp");
  if (fs.existsSync(legacy)) {
    fs.unlinkSync(legacy);
    console.log(`Removed legacy ${folder}/ic_app_logo.webp`);
  }
}

const meta = await sharp(source).metadata();
console.log(
  `Source ${path.basename(source)} ${meta.width}x${meta.height} → square ${EXPORT_SIZE}px webp (no processing)`,
);

const logoBuffer = await buildSquareLogoBuffer(EXPORT_SIZE);
await writeLogo("drawable-nodpi", "ic_app_logo_original_carre", logoBuffer);
await writeLogo("drawable-nodpi", "ic_launcher_foreground", logoBuffer);
