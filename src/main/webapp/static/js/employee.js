document.addEventListener('DOMContentLoaded', () => {
    const jobSelect = document.getElementById('jobOptionId');
    const hireDateInput = document.getElementById('hireDate');
    const codeInput = document.getElementById('employeeCode');
    const idInput = document.querySelector('input[name="id"]');

    const updateCode = () => {
        if (!jobSelect || !hireDateInput || !codeInput) {
            return;
        }
        const job = jobSelect.value;
        const hireDate = hireDateInput.value;
        if (!job || !hireDate) {
            return;
        }
        if (idInput && idInput.value) {
            return; // 编辑时不自动更新编号
        }
        const base = typeof contextPath !== 'undefined' ? contextPath : '';
        const url = `${base}/employees?action=generateCode&jobOptionId=${encodeURIComponent(job)}&hireDate=${encodeURIComponent(hireDate)}`;
        fetch(url)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    codeInput.value = data.code;
                } else if (data.message) {
                    alert(data.message);
                }
            })
            .catch(() => {
                alert('无法生成编号，请稍后再试');
            });
    };

    if (jobSelect) {
        jobSelect.addEventListener('change', updateCode);
    }
    if (hireDateInput) {
        hireDateInput.addEventListener('change', updateCode);
    }

    updateCode();
});
