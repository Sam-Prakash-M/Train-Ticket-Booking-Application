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


function postToServlet(url, amount) {
    const form = document.createElement("form");
    form.method = "POST";
    form.action = url;

    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "amount";
    input.value = amount;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}

document.querySelectorAll(".gateway-btn").forEach(btn => {
    btn.addEventListener("click", addRippleEffect);
});

function payWithRazorpay(amount) {
      postToServlet("RazorPayPayment", amount);
}

function payWithCashfree(amount) {
   postToServlet("CashfreePaymentServlet", amount);
}

function payWithPaypal(amount) {
    postToServlet("PaypalServlet", amount);
}
