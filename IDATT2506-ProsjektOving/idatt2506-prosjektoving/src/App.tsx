import React, { useState, useEffect } from 'react';
import {IonApp, IonContent, IonHeader, IonTitle, IonToolbar} from "@ionic/react";
import { ToDoList } from "./models/types"
import ListTabs from './components/ListTabs';
import TodoInput from './components/TodoInput';
import TodoListView from './components/TodoListView';
import { readLists, saveLists } from './utils/fileStorage';

//Tested on Android Emulator version: TODO: Fyll inn versjon her etterhvert

const App: React.FC = () => {
    const [lists, setLists] = useState<ToDoList[]>([]);
    const [activeIndex, setActiveIndex] = useState<number>(0);

    //KRAV 9: Load saved lists
    useEffect(() => {
        readLists().then(setLists);
    }, []);

    //KRAV 9: Save lists on change
    useEffect(() => {
        saveLists(lists);
    }, [lists]);

    //KRAV 1: Add a new list
    const addList = (name: string) => {
        setLists([...lists, { name, items: [] }]);
        setActiveIndex(lists.length);
    }

    //KRAV 2: Delete a list
    const deleteList = (index: number) => {
        const newLists = [...lists];
        newLists.splice(index, 1);
        setLists(newLists);
        setActiveIndex(0);
    };

    //KRAV 4 og 5: Add a new item to the active list
    const addTodo = (text: string) => {
        const newLists = [...lists];
        newLists[activeIndex ].items.unshift ({ id: Date.now().toString(), text, done: false });
        setLists(newLists);
    };

    //KRAV 7: Toggle item done status
    const toggleDone = (id: string) => {
        const newLists = [...lists];
        const item = newLists[activeIndex ].items.find(i => i.id === id);
        if (item) {
            item.done = !item.done;
            //KRAV 6:  Move finished items to the bottom
            newLists[activeIndex ].items.sort((a, b) => Number(a.done) - Number(b.done));
            setLists(newLists);
        }
    };

    return (
        <IonApp>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>Handle-/Todoliste</IonTitle>
                </IonToolbar>
            </IonHeader>
            <IonContent className="ion-padding">
                {/* KRAV 3: Overview of lists using tabs */}
                <ListTabs lists={lists} activeIndex={activeIndex } setActiveIndex={setActiveIndex } addList={addList} deleteList={deleteList}/>
                {/* KRAV 4: Input field for adding new items */}
                <TodoInput addTodo={addTodo}/>
                {/* KRAV 6: Display list items */}
                {lists[activeIndex ] && <TodoListView items={lists[activeIndex ].items} toggleDone={toggleDone}/>}
            </IonContent>
        </IonApp>
    );
};

export default App;
