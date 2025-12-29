import { request } from "./client.js";

export function loginRequest(payload) {
  return request({
    method: "POST",
    url: "/auth/login",
    data: payload
  });
}

export function registerRequest(payload) {
  return request({
    method: "POST",
    url: "/auth/register",
    data: payload
  });
}
