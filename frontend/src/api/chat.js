import { request } from "./client.js";

export function sendMessage(appointmentId, payload) {
  return request({ method: "POST", url: `/chat/appointments/${appointmentId}/messages`, data: payload });
}

export function fetchMessages(appointmentId, after) {
  return request({
    method: "GET",
    url: `/chat/appointments/${appointmentId}/messages`,
    params: after ? { after } : undefined
  });
}

export function fetchUnreadMessages(appointmentId) {
  return request({ method: "GET", url: `/chat/appointments/${appointmentId}/messages/unread` });
}

export function markMessagesRead(appointmentId, payload) {
  return request({ method: "POST", url: `/chat/appointments/${appointmentId}/messages/read`, data: payload });
}
