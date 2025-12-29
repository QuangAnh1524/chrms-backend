import { request } from "./client.js";

export function createAppointment(payload) {
  return request({ method: "POST", url: "/patients/appointments", data: payload });
}

export function fetchUpcomingAppointments() {
  return request({ method: "GET", url: "/patients/appointments/upcoming" });
}

export function fetchAppointmentHistory() {
  return request({ method: "GET", url: "/patients/appointments/history" });
}

export function fetchDoctorUpcomingAppointments() {
  return request({ method: "GET", url: "/doctors/appointments/upcoming" });
}

export function fetchAppointmentById(id) {
  return request({ method: "GET", url: `/appointments/${id}` });
}

export function confirmAppointment(id) {
  return request({ method: "POST", url: `/appointments/${id}/confirm` });
}

export function completeAppointment(id) {
  return request({ method: "POST", url: `/appointments/${id}/complete` });
}

export function cancelAppointment(id, payload) {
  return request({ method: "POST", url: `/appointments/${id}/cancel`, data: payload });
}
