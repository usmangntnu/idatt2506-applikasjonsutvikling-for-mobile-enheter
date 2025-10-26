import React, { useState, useEffect } from 'react';
import { IonApp} from "@ionic/react";
import { ToDoList } from "./models/types"
import ListTabs from './components/ListTabs';
import TodoInput from './components/TodoInput';
import TodoListView from './components/TodoListView';
import { readLists, saveLists } from './utils/fileStorage';

const App: React.FC = () => {
    const [lists, setLists] = useState<ToDoList[]>([]);
    const [activeListId, setActiveListId] = useState<number>(0);

    // Load saved lists
    useEffect(() => {
        readLists().then(setLists);
    }, []);

    // Save lists on change
    useEffect(() => {
        saveLists(lists);
    }, [lists]);

    // Add a new list
    const addList = (name: string) => {
        setLists([...lists, { name, items: [] }]);
        setActiveListId(lists.length);
    }

    // Delete a list
    const deleteList = (index: number) => {
        const newLists = [...lists];
        newLists.splice(index, 1);
        setLists(newLists);
        setActiveListIndex(0);
    };

    // Add a new item to the active list
    const addTodo = (text: string) => {
        const newLists = [...lists];
        newLists[activeListIndex].items.unshift ({ id: Date.now().toString(), text, done: false });
        setLists(newLists);
    };

    // Toggle item done status
    const toggleDone = (id: string) => {
        const newLists = [...lists];
        const item = newLists[activeListIndex].items.find(i => i.id === id);
        if (item) {
            item.done = !item.done;
            // Move finished items to the bottom
            newLists[activeListIndex].items.sort((a, b) => Number(a.done) - Number(b.done));
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
                <ListTabs lists={lists} activeIndex={activeListIndex} setActiveIndex={setActiveListIndex} addList={addList} deleteList={deleteList}/>
                <TodoInput addTodo={addTodo}/>
                {lists[activeListIndex] && <TodoListView items={lists[activeListIndex].items} toggleDone={toggleDone}/>}
            </IonContent>
        </IonApp>
    );
};

export default App;
