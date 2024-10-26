const fs = require('fs');

// Get the target from environment variable or default to localhost
const target = process.env.BACKEND_API_URL || 'http://localhost:8080';

const proxyConfig = {
  "/api": {
    "target": target,
    "secure": false,
    "logLevel": "debug",
    "changeOrigin": true,
    "pathRewrite": {
      "^/api": ""
    }
  }
};

// Write to proxy.config.json
fs.writeFileSync('proxy.conf.json', JSON.stringify(proxyConfig, null, 2));
console.log('proxy.config.json has been generated with target:', target);
