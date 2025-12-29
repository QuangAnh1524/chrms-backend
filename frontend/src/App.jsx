import { Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Hospitals from "./pages/Hospitals.jsx";
import Doctors from "./pages/Doctors.jsx";
import Appointments from "./pages/Appointments.jsx";
import Payments from "./pages/Payments.jsx";
import MedicalRecords from "./pages/MedicalRecords.jsx";
import Chat from "./pages/Chat.jsx";
import Feedback from "./pages/Feedback.jsx";
import Profile from "./pages/Profile.jsx";

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/hospitals" element={<Hospitals />} />
        <Route path="/doctors" element={<Doctors />} />
        <Route path="/appointments" element={<Appointments />} />
        <Route path="/payments" element={<Payments />} />
        <Route path="/records" element={<MedicalRecords />} />
        <Route path="/chat" element={<Chat />} />
        <Route path="/feedback" element={<Feedback />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}
