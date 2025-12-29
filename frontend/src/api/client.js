import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1"
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("chrms_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export async function request(config) {
  const response = await api(config);
  return response.data;
}

export default api;
