
import { Deletion, Insertion, Move, Update, diff as heckelDiff } from 'heckel-diff-items'

export function diff<T>(oldItems: T[],
                        newItems: T[],
                        keyFunction: (value: T) => any): [number[], number[], Map<number, number>] {
    const ops = heckelDiff(oldItems, newItems, keyFunction)

    const removed: number[] = []
    const added: number[] = []
    const moved = new Map<number, number>()

    ops.forEach((op) => {
        if (op instanceof Deletion) {
            removed.push(op.index)
        } else if (op instanceof Insertion) {
            added.push(op.index)
        }  else if (op instanceof Move) {
            moved.set(op.fromIndex, op.toIndex)
        } else if (op instanceof Update) {
            removed.push(op.index)
            added.push(op.index)
        }
    })

    return [removed, added, moved]
}
