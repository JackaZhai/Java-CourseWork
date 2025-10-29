(() => {
    const app = {};

    app.getContextPath = () => {
        const path = window.location.pathname;
        const lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return '';
        }
        return path.substring(0, lastSlash);
    };

    app.buildUrl = (path, params = {}) => {
        const base = app.getContextPath();
        const url = new URL(base + path, window.location.origin);
        Object.keys(params).forEach(key => {
            if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
                url.searchParams.append(key, params[key]);
            }
        });
        return url.toString();
    };

    app.jsonFetch = (path, { method = 'GET', params = {}, body, headers = {} } = {}) => {
        const url = app.buildUrl(path, params);
        const options = {
            method,
            headers: Object.assign({
                'Accept': 'application/json'
            }, headers),
            credentials: 'include'
        };
        if (body !== undefined) {
            options.body = typeof body === 'string' ? body : JSON.stringify(body);
            if (!options.headers['Content-Type']) {
                options.headers['Content-Type'] = 'application/json';
            }
        }
        return fetch(url, options).then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok || data.success === false) {
                const error = new Error(data.message || '请求失败');
                error.response = data;
                error.status = response.status;
                throw error;
            }
            return data;
        });
    };

    app.showMessage = (container, message, type = 'error') => {
        if (!container) {
            return;
        }
        container.textContent = message;
        container.classList.remove('alert-error', 'alert-success');
        if (message) {
            container.style.display = 'block';
            container.classList.add(type === 'success' ? 'alert-success' : 'alert-error');
        } else {
            container.style.display = 'none';
        }
    };

    window.app = app;
})();
