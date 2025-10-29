document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const errorBox = document.getElementById('loginError');

    form.addEventListener('submit', (event) => {
        event.preventDefault();
        const payload = {
            username: form.username.value.trim(),
            password: form.password.value
        };
        app.jsonFetch('/login', {
            method: 'POST',
            body: payload
        }).then(result => {
            const redirect = result.redirect || app.buildUrl('/employees.html');
            window.location.href = redirect;
        }).catch(error => {
            app.showMessage(errorBox, error.message || '登陆失败');
        });
    });
});
