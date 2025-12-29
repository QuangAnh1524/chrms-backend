import { request } from "./client.js";

export function fetchHospitals() {
  return request({ method: "GET", url: "/hospitals" });
}

export function fetchHospital(id) {
  return request({ method: "GET", url: `/hospitals/${id}` });
}
