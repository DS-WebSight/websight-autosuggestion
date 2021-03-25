const optionIcon = (optionData) => {
    switch (optionData.type) {
    case 'user':
        return 'person';
    case 'group':
        return 'group';
    case 'system_user':
        return 'settings'
    default:
        return '';
    }
}

export const optionData = (option) => ({
    ...option,
    displayName: option.data.displayName || option.value,
    icon: optionIcon(option.data),
    value: option.value
});