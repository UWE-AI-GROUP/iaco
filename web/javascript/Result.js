/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(function () { // on dom ready

    var view = iACO_View({
        container: document.getElementById('view'),
        style: [
            {
                selector: 'node',
                css: {
                    'content': 'data(id)',
                    'text-valign': 'center',
                    'text-halign': 'center'
                }
            },
            {
                selector: '$node > node',
                css: {
                    'padding-top': '10px',
                    'padding-left': '10px',
                    'padding-bottom': '10px',
                    'padding-right': '10px',
                    'text-valign': 'top',
                    'text-halign': 'center'
                }
            },
            {
                selector: 'edge',
                css: {
                    'target-arrow-shape': 'triangle'
                }
            },
            {
                selector: ':selected',
                css: {
                    'background-color': 'black',
                    'line-color': 'black',
                    'target-arrow-color': 'black',
                    'source-arrow-color': 'black'
                }
            }
        ],
        elements: {
            nodes: [
                {data: {id: 'a', parent: 'b'}},
                {data: {id: 'b'}},
                {data: {id: 'c', parent: 'b'}},
                {data: {id: 'd'}},
                {data: {id: 'e'}},
                {data: {id: 'f', parent: 'e'}}
            ],
            edges: [
                {data: {id: 'ad', source: 'a', target: 'd'}},
                {data: {id: 'eb', source: 'e', target: 'b'}}

            ]
        },
        layout: {
            name: 'cose',
            padding: 5
        }
    });

}); // on dom ready

