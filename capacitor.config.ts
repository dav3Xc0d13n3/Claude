import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.example.aiworkspace',
  appName: 'AI Workspace',
  webDir: 'public',
  server: {
    androidScheme: 'https'
  },
  plugins: {
    Keyboard: {
      resize: 'body',
      style: 'dark',
      resizeOnFullScreen: true
    },
    SplashScreen: {
      launchShowDuration: 2000,
      launchAutoHide: true,
      backgroundColor: '#1C1917',
      androidSplashResourceName: 'splash',
      showSpinner: false
    }
  }
};

export default config;
