import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

// React 애플리케이션 엔트리 포인트(Entry Point)
// DOM 루트 요소 선택 및 React 18 Concurrent Mode 초기화
// 잠재적 문제 탐지용 StrictMode 전체 앱 래핑
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
