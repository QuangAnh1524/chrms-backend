import { request } from "./client.js";

export function createPayment(payload) {
  return request({ method: "POST", url: "/payments", data: payload });
}

export function fetchPaymentByAppointment(appointmentId) {
  return request({ method: "GET", url: `/payments/appointment/${appointmentId}` });
}

export function completePayment(transactionRef) {
  return request({ method: "POST", url: `/payments/${transactionRef}/complete` });
}
