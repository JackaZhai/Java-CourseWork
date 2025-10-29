document.addEventListener('DOMContentLoaded', () => {
    const state = {
        page: 1,
        size: 10,
        filters: {}
    };

    const elements = {
        filterForm: document.getElementById('optionFilterForm'),
        resetBtn: document.getElementById('resetOptionFilters'),
        tableBody: document.getElementById('optionTableBody'),
        pagination: document.getElementById('optionPagination'),
        message: document.getElementById('optionMessage'),
        createBtn: document.getElementById('createOptionBtn'),
        logoutBtn: document.getElementById('logoutBtn'),
        modal: document.getElementById('optionModal'),
        modalTitle: document.getElementById('optionModalTitle'),
        modalClose: document.getElementById('optionModalClose'),
        modalMessage: document.getElementById('optionModalMessage'),
        form: document.getElementById('optionForm'),
        cancelBtn: document.getElementById('optionCancelBtn'),
        id: document.getElementById('optionId'),
        name: document.getElementById('optionName'),
        category: document.getElementById('optionCategory'),
        value: document.getElementById('optionValue'),
        remark: document.getElementById('optionRemark')
    };

    const loadList = () => {
        const params = Object.assign({
            action: 'list',
            page: state.page,
            size: state.size,
            format: 'json'
        }, state.filters);
        app.jsonFetch('/options', { params })
            .then(result => {
                const data = result.data || {};
                const pageResult = data.pageResult || { records: [], totalPages: 0, page: 1 };
                updateFilterValues(data.filters || {});
                renderTable(pageResult.records || []);
                renderPagination(pageResult);
                app.showMessage(elements.message, '', 'success');
            })
            .catch(error => {
                renderTable([]);
                elements.pagination.innerHTML = '';
                app.showMessage(elements.message, error.message || '加载失败');
            });
    };

    const updateFilterValues = (filters) => {
        elements.filterForm.elements['name'].value = filters.name || '';
        elements.filterForm.elements['category'].value = filters.category || '';
    };

    const renderTable = (records) => {
        if (!records.length) {
            elements.tableBody.innerHTML = '<tr><td colspan="5" class="text-center">暂无数据</td></tr>';
            return;
        }
        elements.tableBody.innerHTML = '';
        records.forEach(record => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${record.name || ''}</td>
                <td>${record.category || ''}</td>
                <td>${record.value || ''}</td>
                <td>${record.remark || ''}</td>
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
        const fragment = document.createDocumentFragment();
        for (let i = 1; i <= pageResult.totalPages; i++) {
            const link = document.createElement('a');
            link.href = '#';
            link.textContent = i;
            link.className = 'page-link' + (i === pageResult.page ? ' active' : '');
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
        const filters = {
            name: elements.filterForm.elements['name'].value.trim(),
            category: elements.filterForm.elements['category'].value.trim()
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

    const toggleModal = (open) => {
        if (open) {
            elements.modal.classList.add('open');
        } else {
            elements.modal.classList.remove('open');
        }
    };

    const fillForm = (option = {}) => {
        elements.id.value = option.id || '';
        elements.name.value = option.name || '';
        elements.category.value = option.category || '';
        elements.value.value = option.value || '';
        elements.remark.value = option.remark || '';
    };

    const openModal = (mode, id) => {
        app.showMessage(elements.modalMessage, '');
        elements.modalTitle.textContent = mode === 'edit' ? '编辑选项' : '新增选项';
        const promise = mode === 'edit'
            ? app.jsonFetch('/options', { params: { action: 'edit', id, format: 'json' } })
            : app.jsonFetch('/options', { params: { action: 'create', format: 'json' } });
        promise.then(result => {
            const data = result.data || {};
            fillForm(data.optionItem || {});
            toggleModal(true);
        }).catch(error => {
            app.showMessage(elements.message, error.message || '无法打开表单');
        });
    };

    const closeModal = () => {
        toggleModal(false);
        elements.form.reset();
        app.showMessage(elements.modalMessage, '');
    };

    const serializeForm = () => ({
        id: elements.id.value || null,
        name: elements.name.value,
        category: elements.category.value,
        value: elements.value.value,
        remark: elements.remark.value
    });

    const saveOption = () => {
        const payload = serializeForm();
        app.jsonFetch('/options', {
            method: 'POST',
            params: { action: 'save' },
            body: payload
        }).then(() => {
            app.showMessage(elements.message, '保存成功', 'success');
            closeModal();
            loadList();
        }).catch(error => {
            app.showMessage(elements.modalMessage, error.message || '保存失败');
        });
    };

    const deleteOption = (id) => {
        if (!id) {
            return;
        }
        if (!window.confirm('确定删除该选项吗？')) {
            return;
        }
        app.jsonFetch('/options', {
            method: 'POST',
            params: { action: 'delete' },
            body: { id }
        }).then(() => {
            app.showMessage(elements.message, '删除成功', 'success');
            loadList();
        }).catch(error => {
            app.showMessage(elements.message, error.message || '删除失败');
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

    elements.resetBtn.addEventListener('click', (event) => {
        event.preventDefault();
        resetFilters();
    });

    elements.tableBody.addEventListener('click', (event) => {
        const target = event.target;
        if (!(target instanceof HTMLElement)) {
            return;
        }
        const action = target.dataset.action;
        const id = target.dataset.id;
        if (action === 'edit') {
            openModal('edit', id);
        } else if (action === 'delete') {
            deleteOption(id);
        }
    });

    elements.createBtn.addEventListener('click', () => openModal('create'));
    elements.logoutBtn.addEventListener('click', logout);
    elements.modalClose.addEventListener('click', closeModal);
    elements.cancelBtn.addEventListener('click', closeModal);
    elements.form.addEventListener('submit', (event) => {
        event.preventDefault();
        saveOption();
    });

    readFilters();
    loadList();
});
