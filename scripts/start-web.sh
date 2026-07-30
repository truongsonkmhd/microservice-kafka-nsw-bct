#!/usr/bin/env bash
# Chạy song song nsw-web (:4200) và bct-web (:4201)
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT/web-ui"

if [[ ! -d node_modules ]]; then
  npm install
fi

cleanup() {
  kill "$NSW_PID" "$BCT_PID" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

npm run start:nsw &
NSW_PID=$!
npm run start:bct &
BCT_PID=$!

echo "NSW Portal: http://localhost:4200"
echo "BCT Portal: http://localhost:4201"
echo "Nhấn Ctrl+C để dừng cả hai."

wait
