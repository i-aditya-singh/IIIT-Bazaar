<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>IIIT Bazaar - Project Overview</title>
  <style>
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background-color: #f4f4f9;
      color: #333;
      padding: 40px;
      line-height: 1.6;
    }
    h1, h2 {
      color: #2c3e50;
    }
    code {
      background-color: #eaeaea;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 90%;
    }
    .section {
      margin-bottom: 40px;
    }
    .features ul {
      list-style-type: disc;
      margin-left: 20px;
    }
    .button {
      display: inline-block;
      background-color: #3498db;
      color: white;
      padding: 10px 16px;
      text-decoration: none;
      border-radius: 6px;
      margin-top: 10px;
    }
    .button:hover {
      background-color: #2980b9;
    }
  </style>
</head>
<body>

  <h1>IIIT Bazaar 🛍️</h1>
  <p><strong>A student marketplace app built for the IIIT campus using Kotlin and Firebase.</strong></p>

  <div class="section">
    <h2>📱 About the App</h2>
    <p><strong>IIIT Bazaar</strong> is a mobile app designed to let students post and discover items for sale. It supports real-time bidding, chat, and personalized user profiles. Built with Jetpack Compose and Firebase, it ensures a smooth and modern user experience.</p>
  </div>

  <div class="section features">
    <h2>✨ Features</h2>
    <ul>
      <li><strong>Post Items:</strong> Upload products for sale with images, descriptions, and pricing.</li>
      <li><strong>Bidding System:</strong> Real-time bidding with configurable closing time (1h, 12h, 24h, or custom).</li>
      <li><strong>Live Bidding View:</strong> Watch live bids update in real-time via Firebase Firestore.</li>
      <li><strong>Realtime Chat:</strong> One-to-one messaging with image sharing and push notifications.</li>
      <li><strong>User Profiles:</strong> View seller/buyer details, profile picture, and contact options.</li>
    </ul>
  </div>

  <div class="section">
    <h2>🛠️ Tech Stack</h2>
    <ul>
      <li><strong>Frontend:</strong> Kotlin, Jetpack Compose</li>
      <li><strong>Backend:</strong> Firebase Firestore, Firebase Authentication, Firebase Cloud Messaging</li>
      <li><strong>Storage:</strong> Firebase Storage for images</li>
    </ul>
  </div>

  <div class="section">
    <h2>🚀 Getting Started</h2>
    <ol>
      <li>Clone the repository: <code>git clone https://github.com/yourusername/iiit-bazaar.git</code></li>
      <li>Open the project in Android Studio.</li>
      <li>Connect to your Firebase project.</li>
      <li>Run the app on an emulator or physical device.</li>
    </ol>
    <a class="button" href="https://github.com/yourusername/iiit-bazaar" target="_blank">View GitHub Repo</a>
  </div>

  <div class="section">
    <h2>🙋‍♂️ Author</h2>
    <p><strong>Aditya Kumar Singh</strong></p>
    <p>B.Tech CSE, IIIT Nagpur</p>
    <p>Competitive Programmer | Android Developer</p>
    <a class="button" href="https://github.com/iadityasingh" target="_blank">GitHub Profile</a>
  </div>

</body>
</html>
