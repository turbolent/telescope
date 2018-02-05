
const identity = (x: any) => x

export function diff<T, U>
    (original: T[], target: T[], keyFunction: (value: T) => U): [number[], number[], Map<number, number>] {

    keyFunction = keyFunction || identity

    const removed: number[] = []
    const added: number[] = []
    const moved: Map<number, number> = new Map()

    const originalMap: Map<U, number> = new Map()
    original.forEach((item, index) => {
        const key = keyFunction(item)
        originalMap.set(key, index)
    })

    const targetMap: Map<U, number> = new Map()
    target.forEach((item, index) => {
        const key = keyFunction(item)
        targetMap.set(key, index)

        const originalIndex = originalMap.get(key)
        if (originalIndex === undefined) {
            added.push(index)
        }
    })

    original.forEach((item, index) => {
        const key = keyFunction(item)
        const targetIndex = targetMap.get(key)
        if (targetIndex === undefined) {
            removed.push(index)
        } else if (targetIndex !== index) {
            moved.set(index, targetIndex)
        }
    })

    return [removed, added, moved]
}
