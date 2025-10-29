document.addEventListener('DOMContentLoaded', () => {
    const state = {
        page: 1,
        size: 10,
        filters: {},
        jobOptions: []
    };

    const elements = {
        filterForm: document.getElementById('filterForm'),
        resetFiltersBtn: document.getElementById('resetFiltersBtn'),
        tableBody: document.getElementById('employeeTableBody'),
        pagination: document.getElementById('pagination'),
        createBtn: document.getElementById('createEmployeeBtn'),
        logoutBtn: document.getElementById('logoutBtn'),
        pageMessage: document.getElementById('pageMessage'),
        modal: document.getElementById('employeeModal'),
        modalTitle: document.getElementById('modalTitle'),
        modalClose: document.getElementById('modalClose'),
        modalMessage: document.getElementById('modalMessage'),
        employeeForm: document.getElementById('employeeForm'),
        cancelModalBtn: document.getElementById('cancelModalBtn'),
        employeeId: document.getElementById('employeeId'),
        employeeCode: document.getElementById('employeeCode'),
        employeeName: document.getElementById('employeeName'),
        employeeGender: document.getElementById('employeeGender'),
        employeeAge: document.getElementById('employeeAge'),
        employeePhone: document.getElementById('employeePhone'),
        employeeHireDate: document.getElementById('employeeHireDate'),
        employeeJob: document.getElementById('employeeJob'),
        employeeSalary: document.getElementById('employeeSalary')
    };

    const renderJobOptions = (select, options, includeEmpty = false) => {
        select.innerHTML = '';
        if (includeEmpty) {
            const empty = document.createElement('option');
            empty.value = '';
            empty.textContent = includeEmpty === true ? '全部' : includeEmpty;
            select.appendChild(empty);
        }
        options.forEach(option => {
            const node = document.createElement('option');
            node.value = option.id;
            node.textContent = option.name;
            select.appendChild(node);
        });
    };

    const loadList = () => {
        const params = Object.assign({
            action: 'list',
            page: state.page,
            size: state.size,
            format: 'json'
        }, state.filters);
        app.jsonFetch('/employees', { params })
            .then(result => {
                const data = result.data || {};
                state.jobOptions = data.jobOptions || [];
                updateFilterValues(data.filters || {});
                renderJobOptions(elements.filterForm.elements['jobOptionId'], state.jobOptions, true);
                elements.filterForm.elements['jobOptionId'].value = state.filters.jobOptionId || '';
                const pageResult = data.pageResult || { records: [], totalPages: 0, page: 1 };
                renderTable(pageResult.records || []);
                renderPagination(pageResult);
                app.showMessage(elements.pageMessage, '', 'success');
            })
            .catch(error => {
                renderTable([]);
                elements.pagination.innerHTML = '';
                app.showMessage(elements.pageMessage, error.message || '加载失败');
            });
    };

    const updateFilterValues = (filters) => {
        const form = elements.filterForm;
        form.elements['name'].value = filters.name || '';
        form.elements['phone'].value = filters.phone || '';
        form.elements['gender'].value = filters.gender || '';
        form.elements['jobOptionId'].value = filters.jobOptionId || '';
        form.elements['hireDateFrom'].value = filters.hireDateFrom || '';
        form.elements['hireDateTo'].value = filters.hireDateTo || '';
        form.elements['salaryMin'].value = filters.salaryMin || '';
        form.elements['salaryMax'].value = filters.salaryMax || '';
    };

    const formatHireDate = (value) => {
        if (!value) {
            return '';
        }
        if (typeof value === 'string' && value.length >= 7) {
            return value.substring(0, 7);
        }
        return value;
    };

    const renderTable = (records) => {
        if (!records.length) {
            elements.tableBody.innerHTML = '<tr><td colspan="9" class="text-center">暂无数据</td></tr>';
            return;
        }
        elements.tableBody.innerHTML = '';
        records.forEach(record => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${record.employeeCode || ''}</td>
                <td>${record.name || ''}</td>
                <td>${record.gender || ''}</td>
                <td>${record.age != null ? record.age : ''}</td>
                <td>${record.phone || ''}</td>
                <td>${formatHireDate(record.hireDate)}</td>
                <td>${record.jobName || ''}</td>
                <td>${record.salary != null ? record.salary : ''}</td>
                <td class="table-actions">
                    <button class="btn btn-link" data-action="edit" data-id="${record.id}">编辑</button>
                    <button class="btn btn-link danger" data-action="delete" data-id="${record.id}">删除</button>
                </td>`;
            elements.tableBody.appendChild(row);
        });
    };

    const renderPagination = (pageResult) => {
        if (!pageResult || pageResult.totalPages <= 1) {
            elements.pagination.innerHTML = '';
            return;
        }
        const totalPages = pageResult.totalPages;
        const current = pageResult.page;
        const fragment = document.createDocumentFragment();
        for (let i = 1; i <= totalPages; i++) {
            const link = document.createElement('a');
            link.href = '#';
            link.textContent = i;
            link.className = 'page-link' + (i === current ? ' active' : '');
            link.addEventListener('click', (event) => {
                event.preventDefault();
                if (i === state.page) {
                    return;
                }
                state.page = i;
                loadList();
            });
            fragment.appendChild(link);
        }
        elements.pagination.innerHTML = '';
        elements.pagination.appendChild(fragment);
    };

    const readFilters = () => {
        const form = elements.filterForm;
        const filters = {
            name: form.elements['name'].value.trim(),
            phone: form.elements['phone'].value.trim(),
            gender: form.elements['gender'].value,
            jobOptionId: form.elements['jobOptionId'].value,
            hireDateFrom: form.elements['hireDateFrom'].value,
            hireDateTo: form.elements['hireDateTo'].value,
            salaryMin: form.elements['salaryMin'].value,
            salaryMax: form.elements['salaryMax'].value
        };
        Object.keys(filters).forEach(key => {
            if (!filters[key]) {
                delete filters[key];
            }
        });
        state.filters = filters;
    };

    const resetFilters = () => {
        elements.filterForm.reset();
        state.filters = {};
        state.page = 1;
        loadList();
    };

    const attachTableEvents = () => {
        elements.tableBody.addEventListener('click', (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement)) {
                return;
            }
            const action = target.dataset.action;
            if (!action) {
                return;
            }
            const id = target.dataset.id;
            if (action === 'edit') {
                openEmployeeModal('edit', id);
            } else if (action === 'delete') {
                deleteEmployee(id);
            }
        });
    };

    const toggleModal = (open) => {
        if (open) {
            elements.modal.classList.add('open');
        } else {
            elements.modal.classList.remove('open');
        }
    };

    const fillForm = (employee = {}) => {
        elements.employeeId.value = employee.id || '';
        elements.employeeCode.value = employee.employeeCode || '';
        elements.employeeName.value = employee.name || '';
        elements.employeeGender.value = employee.gender || '';
        elements.employeeAge.value = employee.age != null ? employee.age : '';
        elements.employeePhone.value = employee.phone || '';
        elements.employeeHireDate.value = formatHireDate(employee.hireDate);
        elements.employeeJob.value = employee.jobOptionId || '';
        elements.employeeSalary.value = employee.salary != null ? employee.salary : '';
    };

    const setupCodeGeneration = () => {
        const updateCode = () => {
            if (!elements.employeeJob.value || !elements.employeeHireDate.value) {
                return;
            }
            if (elements.employeeId.value) {
                return;
            }
            app.jsonFetch('/employees', {
                params: {
                    action: 'generateCode',
                    jobOptionId: elements.employeeJob.value,
                    hireDate: elements.employeeHireDate.value
                }
            }).then(result => {
                if (result.code) {
                    elements.employeeCode.value = result.code;
                }
            }).catch(() => {
                app.showMessage(elements.modalMessage, '无法生成编号，请稍后重试');
            });
        };
        elements.employeeJob.addEventListener('change', updateCode);
        elements.employeeHireDate.addEventListener('change', updateCode);
    };

    const openEmployeeModal = (mode, id) => {
        app.showMessage(elements.modalMessage, '');
        const title = mode === 'edit' ? '编辑员工' : '新增员工';
        elements.modalTitle.textContent = title;

        const loadPromise = mode === 'edit'
            ? app.jsonFetch('/employees', { params: { action: 'edit', id, format: 'json' } })
            : app.jsonFetch('/employees', { params: { action: 'create', format: 'json' } });

        loadPromise.then(result => {
            const data = result.data || {};
            const options = data.jobOptions || state.jobOptions || [];
            state.jobOptions = options;
            renderJobOptions(elements.employeeJob, options, '请选择');
            if (!options.length) {
                const empty = document.createElement('option');
                empty.value = '';
                empty.textContent = '暂无岗位';
                elements.employeeJob.appendChild(empty);
            }
            fillForm(data.employee || {});
            toggleModal(true);
        }).catch(error => {
            app.showMessage(elements.pageMessage, error.message || '无法打开表单');
        });
    };

    const closeModal = () => {
        toggleModal(false);
        elements.employeeForm.reset();
        app.showMessage(elements.modalMessage, '');
    };

    const serializeEmployeeForm = () => ({
        id: elements.employeeId.value || null,
        employeeCode: elements.employeeCode.value,
        name: elements.employeeName.value,
        gender: elements.employeeGender.value,
        age: elements.employeeAge.value,
        phone: elements.employeePhone.value,
        hireDate: elements.employeeHireDate.value,
        jobOptionId: elements.employeeJob.value,
        salary: elements.employeeSalary.value
    });

    const saveEmployee = () => {
        const payload = serializeEmployeeForm();
        app.jsonFetch('/employees', {
            method: 'POST',
            params: { action: 'save' },
            body: payload
        }).then(() => {
            app.showMessage(elements.pageMessage, '保存成功', 'success');
            closeModal();
            loadList();
        }).catch(error => {
            app.showMessage(elements.modalMessage, error.message || '保存失败');
        });
    };

    const deleteEmployee = (id) => {
        if (!id) {
            return;
        }
        if (!window.confirm('确定删除该员工吗？')) {
            return;
        }
        app.jsonFetch('/employees', {
            method: 'POST',
            params: { action: 'delete' },
            body: { id }
        }).then(() => {
            app.showMessage(elements.pageMessage, '删除成功', 'success');
            loadList();
        }).catch(error => {
            app.showMessage(elements.pageMessage, error.message || '删除失败');
        });
    };

    const logout = () => {
        app.jsonFetch('/logout')
            .then(result => {
                const redirect = result.redirect || app.buildUrl('/login.html');
                window.location.href = redirect;
            })
            .catch(() => {
                window.location.href = app.buildUrl('/login.html');
            });
    };

    elements.filterForm.addEventListener('submit', (event) => {
        event.preventDefault();
        readFilters();
        state.page = 1;
        loadList();
    });

    elements.resetFiltersBtn.addEventListener('click', (event) => {
        event.preventDefault();
        resetFilters();
    });

    elements.createBtn.addEventListener('click', () => openEmployeeModal('create'));
    elements.logoutBtn.addEventListener('click', logout);
    elements.modalClose.addEventListener('click', closeModal);
    elements.cancelModalBtn.addEventListener('click', closeModal);

    elements.employeeForm.addEventListener('submit', (event) => {
        event.preventDefault();
        saveEmployee();
    });

    attachTableEvents();
    setupCodeGeneration();
    readFilters();
    loadList();
});
