import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import {
  approveMedicalRecord,
  createMedicalRecord,
  createPrescription,
  fetchMedicalRecord,
  fetchMedicalRecordFiles,
  fetchMedicalRecordsByPatient,
  fetchMyShares,
  fetchPrescription,
  fetchSharedToMe,
  revokeShare,
  shareMedicalRecord,
  updateMedicalRecord,
  uploadMedicalRecordFile
} from "../api/records.js";

const initialRecord = {
  appointmentId: "",
  symptoms: "",
  diagnosis: "",
  treatment: "",
  notes: ""
};

export default function MedicalRecords() {
  const [recordForm, setRecordForm] = useState(initialRecord);
  const [recordId, setRecordId] = useState("");
  const [patientId, setPatientId] = useState("");
  const [shareId, setShareId] = useState("");
  const [shareHospitalId, setShareHospitalId] = useState("");
  const [prescriptionItems, setPrescriptionItems] = useState(`{\n  \"items\": [\n    {\n      \"medicineId\": 1,\n      \"dosage\": \"1 viên\",\n      \"frequency\": \"2 lần/ngày\",\n      \"duration\": \"5 ngày\",\n      \"quantity\": 10,\n      \"instructions\": \"Sau ăn\"\n    }\n  ]\n}`);
  const [recordDetail, setRecordDetail] = useState(null);
  const [recordList, setRecordList] = useState([]);
  const [recordFiles, setRecordFiles] = useState([]);
  const [prescription, setPrescription] = useState(null);
  const [sharedToMe, setSharedToMe] = useState([]);
  const [myShares, setMyShares] = useState([]);
  const [error, setError] = useState("");

  const onChange = (event) => {
    setRecordForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleCreate = async (event) => {
    event.preventDefault();
    setError("");
    try {
      const data = await createMedicalRecord({
        appointmentId: Number(recordForm.appointmentId),
        symptoms: recordForm.symptoms || undefined,
        diagnosis: recordForm.diagnosis || undefined,
        treatment: recordForm.treatment || undefined,
        notes: recordForm.notes || undefined
      });
      setRecordDetail(data);
      setRecordForm(initialRecord);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tạo hồ sơ");
    }
  };

  const handleUpdate = async () => {
    if (!recordId) {
      return;
    }
    setError("");
    try {
      const data = await updateMedicalRecord(recordId, {
        symptoms: recordForm.symptoms || undefined,
        diagnosis: recordForm.diagnosis || undefined,
        treatment: recordForm.treatment || undefined,
        notes: recordForm.notes || undefined
      });
      setRecordDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không cập nhật hồ sơ");
    }
  };

  const handleApprove = async () => {
    if (!recordId) {
      return;
    }
    setError("");
    try {
      const data = await approveMedicalRecord(recordId);
      setRecordDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không duyệt hồ sơ");
    }
  };

  const handleFetchRecord = async () => {
    if (!recordId) {
      return;
    }
    setError("");
    try {
      const data = await fetchMedicalRecord(recordId);
      setRecordDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy hồ sơ");
    }
  };

  const handleFetchPatientRecords = async () => {
    if (!patientId) {
      return;
    }
    setError("");
    try {
      const data = await fetchMedicalRecordsByPatient(patientId);
      setRecordList(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy danh sách hồ sơ");
    }
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files?.[0];
    if (!file || !recordId) {
      return;
    }
    setError("");
    try {
      const formData = new FormData();
      formData.append("medicalRecordId", recordId);
      formData.append("file", file);
      formData.append("fileType", "LAB_RESULT");
      const data = await uploadMedicalRecordFile(formData);
      setRecordFiles((prev) => [...prev, data]);
    } catch (err) {
      setError(err?.response?.data?.message || "Không upload được file");
    }
  };

  const handleFetchFiles = async () => {
    if (!recordId) {
      return;
    }
    setError("");
    try {
      const data = await fetchMedicalRecordFiles(recordId);
      setRecordFiles(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy file");
    }
  };

  const handleCreatePrescription = async () => {
    if (!recordId) {
      return;
    }
    setError("");
    try {
      const payload = JSON.parse(prescriptionItems);
      const data = await createPrescription({ medicalRecordId: Number(recordId), ...payload });
      setPrescription(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tạo đơn thuốc");
    }
  };

  const handleFetchPrescription = async () => {
    if (!recordId) {
      return;
    }
    setError("");
    try {
      const data = await fetchPrescription(recordId);
      setPrescription(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy đơn thuốc");
    }
  };

  const handleShare = async () => {
    if (!recordId || !shareHospitalId) {
      return;
    }
    setError("");
    try {
      const data = await shareMedicalRecord(recordId, {
        toHospitalId: Number(shareHospitalId),
        notes: "Chia sẻ từ FE"
      });
      setMyShares((prev) => [data, ...prev]);
    } catch (err) {
      setError(err?.response?.data?.message || "Không thể chia sẻ");
    }
  };

  const handleFetchSharedToMe = async () => {
    setError("");
    try {
      const data = await fetchSharedToMe(patientId || undefined);
      setSharedToMe(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy danh sách chia sẻ");
    }
  };

  const handleFetchMyShares = async () => {
    setError("");
    try {
      const data = await fetchMyShares();
      setMyShares(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy danh sách chia sẻ" );
    }
  };

  const handleRevoke = async () => {
    if (!shareId) {
      return;
    }
    setError("");
    try {
      await revokeShare(shareId);
      setMyShares((prev) => prev.filter((item) => String(item.id) !== String(shareId)));
    } catch (err) {
      setError(err?.response?.data?.message || "Không thu hồi được" );
    }
  };

  return (
    <>
      <Section title="Tạo hồ sơ khám">
        <form onSubmit={handleCreate}>
          <div className="grid two">
            <Field label="Appointment ID">
              <input name="appointmentId" value={recordForm.appointmentId} onChange={onChange} />
            </Field>
            <Field label="Triệu chứng">
              <input name="symptoms" value={recordForm.symptoms} onChange={onChange} />
            </Field>
            <Field label="Chẩn đoán">
              <input name="diagnosis" value={recordForm.diagnosis} onChange={onChange} />
            </Field>
            <Field label="Điều trị">
              <input name="treatment" value={recordForm.treatment} onChange={onChange} />
            </Field>
            <Field label="Ghi chú">
              <input name="notes" value={recordForm.notes} onChange={onChange} />
            </Field>
          </div>
          {error ? <p className="muted">{error}</p> : null}
          <button className="button" type="submit">
            Tạo hồ sơ
          </button>
        </form>
      </Section>

      <Section title="Quản lý hồ sơ">
        <Field label="Medical Record ID">
          <input value={recordId} onChange={(event) => setRecordId(event.target.value)} />
        </Field>
        <div className="inline-actions">
          <button className="button" type="button" onClick={handleFetchRecord}>
            Lấy chi tiết
          </button>
          <button className="button outline" type="button" onClick={handleUpdate}>
            Cập nhật
          </button>
          <button className="button secondary" type="button" onClick={handleApprove}>
            Duyệt hồ sơ
          </button>
        </div>
        {recordDetail ? (
          <Card title={`Record #${recordDetail.id}`} subtitle={`Status: ${recordDetail.status}`}>
            <p className="muted">Appointment: {recordDetail.appointmentId}</p>
            <p className="muted">Diagnosis: {recordDetail.diagnosis || "-"}</p>
          </Card>
        ) : null}
      </Section>

      <Section title="Danh sách hồ sơ theo bệnh nhân">
        <Field label="Patient ID">
          <input value={patientId} onChange={(event) => setPatientId(event.target.value)} />
        </Field>
        <button className="button" type="button" onClick={handleFetchPatientRecords}>
          Lấy hồ sơ
        </button>
        <div className="grid two">
          {recordList.map((record) => (
            <Card key={record.id} title={`Record #${record.id}`} subtitle={`Status: ${record.status}`}>
              <p className="muted">Appointment: {record.appointmentId}</p>
            </Card>
          ))}
        </div>
      </Section>

      <Section title="File đính kèm">
        <div className="inline-actions">
          <input type="file" onChange={handleFileUpload} />
          <button className="button outline" type="button" onClick={handleFetchFiles}>
            Tải file
          </button>
        </div>
        {recordFiles.length ? (
          <ul>
            {recordFiles.map((file) => (
              <li key={file.id || file.fileName}>{file.fileName || file.originalFileName}</li>
            ))}
          </ul>
        ) : (
          <p className="muted">Chưa có file.</p>
        )}
      </Section>

      <Section title="Đơn thuốc">
        <Field label="Cấu trúc JSON items">
          <textarea value={prescriptionItems} onChange={(event) => setPrescriptionItems(event.target.value)} />
        </Field>
        <div className="inline-actions">
          <button className="button" type="button" onClick={handleCreatePrescription}>
            Tạo đơn thuốc
          </button>
          <button className="button outline" type="button" onClick={handleFetchPrescription}>
            Lấy đơn thuốc
          </button>
        </div>
        {prescription ? (
          <Card title={`Prescription #${prescription.id || ""}`} subtitle={`Items: ${prescription.items?.length || 0}`}>
            <pre>{JSON.stringify(prescription, null, 2)}</pre>
          </Card>
        ) : null}
      </Section>

      <Section title="Chia sẻ hồ sơ">
        <div className="grid two">
          <div>
            <Field label="Hospital ID nhận">
              <input value={shareHospitalId} onChange={(event) => setShareHospitalId(event.target.value)} />
            </Field>
            <button className="button" type="button" onClick={handleShare}>
              Chia sẻ hồ sơ
            </button>
          </div>
          <div>
            <Field label="Share ID">
              <input value={shareId} onChange={(event) => setShareId(event.target.value)} />
            </Field>
            <button className="button secondary" type="button" onClick={handleRevoke}>
              Thu hồi
            </button>
          </div>
        </div>
        <div className="inline-actions" style={{ marginTop: 12 }}>
          <button className="button outline" type="button" onClick={handleFetchSharedToMe}>
            Hồ sơ shared tới tôi
          </button>
          <button className="button outline" type="button" onClick={handleFetchMyShares}>
            Hồ sơ tôi đã share
          </button>
        </div>
        <div className="grid two">
          <Card title="Shared to me">
            {sharedToMe.length ? (
              <ul>
                {sharedToMe.map((share) => (
                  <li key={share.id}>Record {share.medicalRecordId} từ hospital {share.fromHospitalId}</li>
                ))}
              </ul>
            ) : (
              <p className="muted">Chưa có dữ liệu.</p>
            )}
          </Card>
          <Card title="My shares">
            {myShares.length ? (
              <ul>
                {myShares.map((share) => (
                  <li key={share.id}>Record {share.medicalRecordId} → hospital {share.toHospitalId}</li>
                ))}
              </ul>
            ) : (
              <p className="muted">Chưa có dữ liệu.</p>
            )}
          </Card>
        </div>
        {error ? <p className="muted">{error}</p> : null}
      </Section>
    </>
  );
}
