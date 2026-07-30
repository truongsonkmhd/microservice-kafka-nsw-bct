# Web UI — NSW & BCT (2 portal riêng)

Monorepo Angular gồm **2 ứng dụng độc lập**, mỗi app proxy tới API backend riêng và dùng **WebSocket** cập nhật realtime.

| App | Port | Vai trò | Backend |
|-----|------|---------|---------|
| **nsw-web** | 4200 | Doanh nghiệp — nộp hồ sơ, tra cứu | nsw-gateway `:8084` |
| **bct-web** | 4201 | Cán bộ BCT — danh sách, duyệt/từ chối | bct-adapter `:8085` |

## Chạy local

**Yêu cầu:** Node 20+, infra (Kafka/MySQL), `nsw-gateway` (8084) + `bct-adapter` (8085).

```bash
# Terminal 1 — NSW (doanh nghiệp)
cd web-ui && npm install && npm run start:nsw
# → http://localhost:4200

# Terminal 2 — BCT (cán bộ)
cd web-ui && npm run start:bct
# → http://localhost:4201
```

Sau khi sửa backend (`common`, websocket), rebuild Java:

```bash
mvn -pl common,nsw-gateway,bct-adapter -am clean install -DskipTests
```

## WebSocket

| Portal | Endpoint | Khi nào push |
|--------|----------|--------------|
| NSW | `ws://localhost:4200/ws/nsw/ho-so/{maSoHoSo}` | Nộp hồ sơ, chuyển trạng thái (poll 2s), nhận TraLoi |
| BCT | `ws://localhost:4201/ws/bct/ho-so/{status}` | Hồ sơ mới CHO_XU_LY, duyệt/từ chối (remove khỏi list) |

Dev proxy: `proxy.nsw.conf.json`, `proxy.bct.conf.json`.

## Routes

### nsw-web (4200)

| Route | Màn hình |
|-------|----------|
| `/nop-ho-so` | Nộp hồ sơ + timeline realtime |
| `/tra-cuu` | Tra cứu + auto-update qua WS |

### bct-web (4201)

| Route | Màn hình |
|-------|----------|
| `/` | Danh sách hồ sơ (filter status) + live list |
| `/ho-so/:maSoHoSo` | Chi tiết, phê duyệt / từ chối |

## Build production

```bash
npm run build          # cả hai app
npm run build:nsw      # dist/nsw-web
npm run build:bct      # dist/bct-web
```

Production API prefix: xem `environment.prod.ts` trong từng app.

## Cấu trúc

```
web-ui/
├── src/                    # nsw-web
├── projects/bct-web/src/   # bct-web
├── proxy.nsw.conf.json
└── proxy.bct.conf.json
```
