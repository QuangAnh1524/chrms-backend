import { request } from "./client.js";

export function fetchDoctors() {
  return request({ method: "GET", url: "/doctors" });
}

export function fetchDoctor(id) {
  return request({ method: "GET", url: `/doctors/${id}` });
}

export function fetchDoctorsByHospital(hospitalId) {
  return request({ method: "GET", url: `/doctors/hospital/${hospitalId}` });
}

export function fetchDoctorsByDepartment(departmentId) {
  return request({ method: "GET", url: `/doctors/department/${departmentId}` });
}

export function createDoctorSchedule(payload) {
  return request({ method: "POST", url: "/doctors/schedules", data: payload });
}

export function fetchDoctorSchedules(doctorId) {
  return request({ method: "GET", url: `/doctors/${doctorId}/schedules` });
}

export function fetchAvailableSlots(doctorId, date) {
  return request({
    method: "GET",
    url: `/doctors/${doctorId}/available-slots`,
    params: { date }
  });
}
