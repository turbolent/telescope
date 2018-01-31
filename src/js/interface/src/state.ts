import { Cancel } from './api';
import { Parse } from './types';
import { Map } from 'immutable';

export class State {
    private readonly map: Map<string, any>;

    constructor(map: Map<string, any> = Map<string, any>()) {
        this.map = map;
    }

    withMutations(mutator: (mutableState: State) => void): State {
        return new State(this.map.withMutations(map =>
            mutator(new State(map))));
    }

    get error(): string | undefined {
        return this.map.get('error');
    }

    withError(error?: string): State {
        return new State(this.map.set('error', error));
    }

    get requesting(): boolean {
        return this.cancel !== undefined;
    }

    get parse(): Parse | undefined {
        return this.map.get('parse');
    }

    withParse(parse: Parse | undefined): State {
        return new State( this.map.set('parse', parse));
    }

    get cancel(): Cancel | undefined {
        return this.map.get('cancel');
    }

    withCancel(cancel: Cancel | undefined): State {
        return new State(this.map.set('cancel', cancel));
    }

    get question(): string {
        return this.map.get('question') || '';
    }

    withQuestion(question: string): State {
        return new State(this.map.set('question', question));
    }
}
