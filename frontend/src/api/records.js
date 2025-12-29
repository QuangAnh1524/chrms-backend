import { request } from "./client.js";

export function createMedicalRecord(payload) {
  return request({ method: "POST", url: "/medical-records", data: payload });
}

export function updateMedicalRecord(id, payload) {
  return request({ method: "PATCH", url: `/medical-records/${id}`, data: payload });
}

export function approveMedicalRecord(id) {
  return request({ method: "POST", url: `/medical-records/${id}/approve` });
}

export function fetchMedicalRecordsByPatient(patientId) {
  return request({ method: "GET", url: `/medical-records/patient/${patientId}` });
}

export function fetchMedicalRecord(id) {
  return request({ method: "GET", url: `/medical-records/${id}` });
}

export function uploadMedicalRecordFile(formData) {
  return request({
    method: "POST",
    url: "/medical-records/files/upload",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" }
  });
}

export function fetchMedicalRecordFiles(recordId) {
  return request({ method: "GET", url: `/medical-records/files/medical-record/${recordId}` });
}

export function createPrescription(payload) {
  return request({ method: "POST", url: "/prescriptions", data: payload });
}

export function fetchPrescription(recordId) {
  return request({ method: "GET", url: `/prescriptions/medical-record/${recordId}` });
}

export function shareMedicalRecord(id, payload) {
  return request({ method: "POST", url: `/medical-records/${id}/share`, data: payload });
}

export function fetchSharedToMe(patientId) {
  return request({
    method: "GET",
    url: "/medical-records/shared-to-me",
    params: patientId ? { patientId } : undefined
  });
}

export function fetchMyShares() {
  return request({ method: "GET", url: "/medical-records/my-shares" });
}

export function revokeShare(id) {
  return request({ method: "DELETE", url: `/medical-records/shares/${id}` });
}
