import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import HomePage from './pages/HomePage.jsx'
import PackageDetailPage from './pages/PackageDetailPage.jsx'
import BookingPage from './pages/BookingPage.jsx'
import PaymentPage from './pages/PaymentPage.jsx'
import PaymentConfirmationPage from './pages/PaymentConfirmationPage.jsx'
import MyBookingsPage from './pages/MyBookingsPage.jsx'
import ReportsPage from './pages/ReportsPage.jsx'

// Componente raiz: define el enrutamiento de toda la aplicacion
function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/home"
            element={
              <ProtectedRoute>
                <HomePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/packages/:id"
            element={
              <ProtectedRoute>
                <PackageDetailPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/booking/:packageId"
            element={
              <ProtectedRoute>
                <BookingPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/payment/:bookingId"
            element={
              <ProtectedRoute>
                <PaymentPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/payment-confirmation/:bookingId"
            element={
              <ProtectedRoute>
                <PaymentConfirmationPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-bookings"
            element={
              <ProtectedRoute>
                <MyBookingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/reports"
            element={
              <ProtectedRoute adminOnly>
                <ReportsPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
