function addRippleEffect(event) {
    const btn = event.currentTarget;
    const circle = document.createElement("span");
    const diameter = Math.max(btn.clientWidth, btn.clientHeight);
    const radius = diameter / 2;

    circle.style.width = circle.style.height = `${diameter}px`;
    circle.style.left = `${event.clientX - btn.offsetLeft - radius}px`;
    circle.style.top = `${event.clientY - btn.offsetTop - radius}px`;
    circle.classList.add("ripple");

    const ripple = btn.getElementsByClassName("ripple")[0];
    if (ripple) ripple.remove();

    btn.appendChild(circle);
}

document.querySelectorAll(".gateway-btn").forEach(btn => {
    btn.addEventListener("click", addRippleEffect);
});

function payWithRazorpay(amount) {
    window.location.href = "razorpay.jsp?amount=" + amount;
}

function payWithCashfree(amount) {
    window.location.href = "cashfree.jsp?amount=" + amount;
}

function payWithPaypal(amount) {
    window.location.href = "paypal.jsp?amount=" + amount;
}
