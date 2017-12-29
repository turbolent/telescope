
export class Wikidata {
    private static readonly QUERY_BASE = 'http://query.wikidata.org/';
    private static readonly WIKI_BASE = 'http://wikidata.org/wiki/';

    static getPropertyURL(id: string) {
        return Wikidata.WIKI_BASE + 'Property:P' + id;
    }

    static getItemURL(id: string) {
        return Wikidata.WIKI_BASE + 'Q' + id;
    }

    static getQueryURL(query: string) {
        return Wikidata.QUERY_BASE + '#' + encodeURIComponent(query);
    }
}
