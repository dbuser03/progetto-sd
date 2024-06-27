document.addEventListener('DOMContentLoaded', async () => {
    const tableBody = document.querySelector('.order-table tbody');

    // Event delegation for hover effect on table rows
    tableBody.addEventListener('mouseenter', event => {
        if (event.target.tagName === 'TR') {
            // Set a timeout to add hover class
            let hoverTimeout = setTimeout(() => {
                event.target.classList.add('hovered');
            }, 400);
            // Store timeout ID for later use
            event.target.dataset.hoverTimeout = hoverTimeout;
        }
    }, true); // Capture phase

    tableBody.addEventListener('mouseleave', event => {
        if (event.target.tagName === 'TR') {
            // Retrieve and clear the hover timeout
            const hoverTimeout = event.target.dataset.hoverTimeout;
            clearTimeout(hoverTimeout);
            event.target.classList.remove('hovered');
        }
    }, true); // Capture phase

    // Fetch and display orders
    try {
        const userId = sessionStorage.getItem('sessionToken');
        const response = await fetch(`http://localhost:8080/orders?userId=${userId}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Server responded with status: ${response.status}`);
        }

        const jsonResponse = await response.json();

        // Clear table and populate with new rows
        tableBody.innerHTML = '';
        jsonResponse.forEach(order => {
            const row = document.createElement('tr');
            row.innerHTML = `
        <td>${order.orderId}</td>
        <td>${order.currentDate}</td>
        <td>${order.expirationDate}</td>
      `;
            tableBody.appendChild(row);

            // Add click event listener to each row
            row.addEventListener('click', () => {
                searchorder(order.orderId, userId);
            });
        });

    } catch (error) {
        alert('Failed to fetch orders: ' + error.message);
    }
});
