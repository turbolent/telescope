export function encodeQuestion(question: string) {
    return '#' + encodeURIComponent(question.trim());
}

export function getSavedQuestion(): string {
    const hash = window.location.hash;
    if (!hash.length) {
        return '';
    }

    return decodeURIComponent(hash.substring(1));
}
