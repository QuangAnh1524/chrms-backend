import { request } from "./client.js";

export function fetchMyProfile() {
  return request({ method: "GET", url: "/patients/me" });
}

export function updateMyProfile(payload) {
  return request({ method: "PATCH", url: "/patients/me", data: payload });
}
