import axios from 'axios'

// Cliente HTTP apuntando al API Gateway
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

// Extrae un mensaje de error legible desde una respuesta de axios.
// El backend responde errores con { message } o, en validaciones, con { errors: { campo: mensaje } }.
export function getErrorMessage(error) {
  const data = error?.response?.data
  if (data?.errors && typeof data.errors === 'object') {
    return Object.values(data.errors).join('. ')
  }
  if (data?.message) return data.message
  if (error?.message) return error.message
  return 'Ocurrio un error inesperado. Intenta nuevamente.'
}

// --- Autenticacion (user-service) ---

export function login(email, password) {
  return api.post('/auth/login', { email, password }).then((res) => res.data)
}

export function register(payload) {
  return api.post('/auth/register', payload).then((res) => res.data)
}

// --- Paquetes (package-service) ---

export function getAvailablePackages() {
  return api.get('/packages/available').then((res) => res.data)
}

export function getPackageById(id) {
  return api.get(`/packages/${id}`).then((res) => res.data)
}

export function searchPackages(filters) {
  const params = {}
  if (filters.destination) params.destination = filters.destination
  if (filters.minPrice !== '' && filters.minPrice != null) params.minPrice = filters.minPrice
  if (filters.maxPrice !== '' && filters.maxPrice != null) params.maxPrice = filters.maxPrice
  if (filters.startDate) params.startDate = filters.startDate
  return api.get('/packages/search', { params }).then((res) => res.data)
}

// Administracion de paquetes (Epica 2): solo ADMIN, revalidado en package-service.
export function getAllPackagesAdmin(role) {
  return api.get('/packages', { params: { role } }).then((res) => res.data)
}

export function createPackage(role, payload) {
  return api.post('/packages', payload, { params: { role } }).then((res) => res.data)
}

export function updatePackage(id, role, payload) {
  return api.put(`/packages/${id}`, payload, { params: { role } }).then((res) => res.data)
}

export function changePackageStatus(id, status, role) {
  return api.patch(`/packages/${id}/status`, null, { params: { status, role } }).then((res) => res.data)
}

// --- Reservas (booking-service) ---

export function createBooking(userId, booking) {
  return api.post('/bookings', booking, { params: { userId } }).then((res) => res.data)
}

// Administracion de reservas (Epica 6): solo ADMIN, revalidado en confirmation-service.
// Via /confirmations (no /bookings) porque ya devuelve el estado legible y el pago
// asociado, igual que "Mis reservas" -- misma fuente de datos, sin filtrar por dueno.
export function getAllBookingsAdmin(role) {
  return api.get('/confirmations', { params: { role } }).then((res) => res.data)
}

// --- Pagos (payment-service) ---

export function getPaymentSummary(bookingId) {
  return api.get(`/payments/summary/${bookingId}`).then((res) => res.data)
}

export function processPayment(payment) {
  return api.post('/payments', payment).then((res) => res.data)
}

// --- Confirmaciones y seguimiento (confirmation-service) ---

export function getPaymentVoucher(bookingId) {
  return api.get(`/confirmations/${bookingId}/voucher`).then((res) => res.data)
}

export function getMyBookings(userId) {
  return api.get('/confirmations/my-bookings', { params: { userId } }).then((res) => res.data)
}

export function cancelBooking(bookingId, userId, role) {
  return api
    .patch(`/confirmations/${bookingId}/cancel`, null, { params: { userId, role } })
    .then((res) => res.data)
}

// --- Reportes (report-service, Epica 7) ---

export function getSalesReport(role, dateRange) {
  return api.post('/reports/sales', dateRange, { params: { role } }).then((res) => res.data)
}

export function getPackageRanking(role, dateRange) {
  return api.post('/reports/ranking', dateRange, { params: { role } }).then((res) => res.data)
}

export default api
