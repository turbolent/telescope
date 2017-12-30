export function encodeSentence(sentence: string) {
    return '#' + encodeURIComponent(sentence.trim())
}

export function getSavedSentence(): string {
    const hash = window.location.hash;
    if (!hash.length) {
        return '';
    }

    return decodeURIComponent(hash.substring(1));
}
