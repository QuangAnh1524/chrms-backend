import { request } from "./client.js";

export function createFeedback(payload) {
  return request({ method: "POST", url: "/feedback", data: payload });
}

export function fetchDoctorFeedback(doctorId) {
  return request({ method: "GET", url: `/feedback/doctor/${doctorId}` });
}

export function fetchDoctorAverageRating(doctorId) {
  return request({ method: "GET", url: `/feedback/doctor/${doctorId}/average-rating` });
}
