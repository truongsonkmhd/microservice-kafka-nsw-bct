import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { HoSoStatusEvent } from '../models/ho-so-status-event.model';
import { BctBusinessStatus } from '../models/business-status.model';

@Injectable({ providedIn: 'root' })
export class BctWebSocketService {
  watchList(status: BctBusinessStatus): Observable<HoSoStatusEvent> {
    return new Observable((subscriber) => {
      const url = buildWebSocketUrl(
        `${environment.wsPath}/ho-so/${encodeURIComponent(status)}`,
      );
      const socket = new WebSocket(url);

      socket.onmessage = (event) => {
        try {
          subscriber.next(JSON.parse(event.data) as HoSoStatusEvent);
        } catch {
          // ignore malformed payloads
        }
      };

      socket.onerror = () => subscriber.error(new Error('WebSocket connection failed'));
      socket.onclose = () => subscriber.complete();

      return () => {
        if (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING) {
          socket.close();
        }
      };
    });
  }
}

function buildWebSocketUrl(path: string): string {
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${protocol}//${window.location.host}${path}`;
}
