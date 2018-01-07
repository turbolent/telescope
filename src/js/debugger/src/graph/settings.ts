import { hsl } from 'd3-color';

function generateShadow(x: number, y: number, blur: number, color: string): string {
    const offsets = [
        ` ${x}px  ${y}px`,
        ` ${x}px -${y}px`,
        `-${x}px  ${y}px`,
        `-${x}px -${y}px`
    ];

    return offsets
        .map(offset => offset + ` ${blur}px ${color}`)
        .join(', ')
}

const settings = {
    size: {
        baseWidth: 140,
        baseHeight: 100,
        adjustValue(value: number, nodeCount: number, edgeCount: number): number {
            const totalCount = nodeCount + edgeCount;
            return Math.round(value * (2 + 0.2 * Math.pow(totalCount, 1.2)))
        }
    },
    layout: {
        manyBodyForceStrength: -30,
        getCollisionRadius: () => settings.node.radius * 3,
        initialForwardPercentage: 0.2,
        relayoutAlpha: 0.8,
        alphaDecay: 0.012,
        dragAlphaTarget: 0.1,
        scaleExtent: <[number, number]>[0.2, 3]
    },
    node: {
        radius: 14,
        backgroundColor: {
            getRoot: () => hsl('#bcdd9f'),
            getVariable: () => hsl('#c8e4fa'),
            getLabel: () => hsl('#fde699'),
            getOther: () => hsl('#ddd'),
            getFilter: () => hsl('#ffc0cb'),
            getAggregate: () => hsl('#CE93D8').brighter(0.5)
        },
        textColor: {
            getRoot: () => settings.node.backgroundColor.getRoot().darker(2),
            getVariable: () => settings.node.backgroundColor.getVariable().darker(1.4),
            getConjunction: () => settings.node.backgroundColor.getOther().darker(2),
            getLink: () => hsl('#1e7aad'),
            getOther: () => hsl('#555')
        },
        stroke: {
            width: 2,
            color: {
                getRoot: () => settings.node.backgroundColor.getRoot().darker(1),
                getVariable: () => settings.node.backgroundColor.getVariable().darker(0.6),
                getLabel: () => settings.node.backgroundColor.getLabel().darker(0.6),
                getAggregate: () => settings.node.backgroundColor.getAggregate().darker(0.4),
                getOther: () => settings.node.backgroundColor.getOther().darker(0.4)
            }
        }
    },
    edge: {
        strokeWidth: 3,
        labelOffsetX: '54%',
        labelOffsetY: -4.6,
        color: {
            getDirected: () => hsl('#6abceb'),
            getFilter: () => settings.node.backgroundColor.getFilter().darker(0.3),
            getAggregate: () => settings.node.backgroundColor.getAggregate().darker(0.3),
            getOther: () => settings.node.backgroundColor.getOther().darker(0.2)
        },
        textColor: {
            getLink: () => settings.node.textColor.getLink(),
            getOther: () => settings.node.textColor.getOther()
        },
        distance: {
            getLabeled: (length: number) => Math.max(100, length * 14),
            getLong: () => 80,
            getShort: () => 20,
        }
    },
    marker: {
        size: 12,
        color: {
            getFilter: () => settings.edge.color.getFilter(),
            getDirected: () => settings.edge.color.getDirected(),
        }
    },
    textShadow: {
        normal: generateShadow(2, 2, 4, 'rgba(255, 255, 255, 0.4)'),
        strong: generateShadow(2, 2, 3, 'white')
    },
    fadeInDuration: 100,
    fadeOutDuration: 100,
    secondaryDashArray: '7,3'
};

export default settings;
