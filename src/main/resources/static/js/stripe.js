const stripe = Stripe('pk_test_51OpllsFOPtWWKDHoGjFt9neEckDASiBf9DB8YINK51D5sfCxTX1xBWtLYRoblEbclog3QVCjHEbgw5HdvsXQchYJ007PECTk2B');
const paymentButton = document.querySelector('#paymentButton')

paymentButton.addEventListener('click', () => {
    stripe.redirectToCheckout({
        sessionId: sessionId
    });
});