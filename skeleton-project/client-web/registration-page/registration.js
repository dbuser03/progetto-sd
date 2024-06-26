function resetRegistrationForm() {
  // Reset the registration form
  document.getElementById('registration-form').reset();
}

// Add event listener for form submission
document.getElementById('registration-form').addEventListener('submit', async function(event) {
  event.preventDefault(); // Prevent default form submission

  // Serialize form data into JSON
  const formData = new FormData(this);
  const jsonData = {};
  
  // Initialize userId as empty string
  jsonData.userId = "";

  formData.forEach((value, key) => {
    jsonData[key] = value;
  });

  // Make POST request to server
  try {
    const response = await fetch('http://localhost:8080/registrations', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(jsonData)
    });

    if (!response.ok) {
      // Extract error details from response if possible
      const errorDetails = await response.text(); // Assuming the server sends a plain text or JSON error message
      throw new Error('Error registering user. Server responded with: ' + errorDetails);
    }

    // Handle successful registration
    const userId = await response.json(); // Extract user ID from response
    console.log('User ID:', userId);
    sessionStorage.setItem('sessionToken', userId); // Store the session token

    // Wait 0.5 second before redirecting
    window.location.replace('./userid.html?userId=' + userId);
    resetRegistrationForm(); // Reset form after successful registration

  } catch (error) {
    console.error('Error:', error.message);
    // Convert jsonData to a readable string for debugging
    const requestBodyDebugInfo = JSON.stringify(jsonData, null, 2); // Pretty print JSON
    // Handle error (e.g., show error message to user with additional debug info if available)
    alert('Error registering user. Please try again. Debug info: ' + error.message + '\nRequest Body: ' + requestBodyDebugInfo);
  }
});