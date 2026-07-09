import axios from 'axios'

// Cliente HTTP apuntando al API Gateway
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export default api
