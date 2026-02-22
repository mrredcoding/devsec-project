import { BrowserRouter } from "react-router-dom";
import { AuthenticationProvider } from "./context/AuthenticationContext";
import AppRouter from "./router/AppRouter";

export default function App() {
  return (
    <BrowserRouter>
      <AuthenticationProvider>
        <AppRouter />
      </AuthenticationProvider>
    </BrowserRouter>
  );
}