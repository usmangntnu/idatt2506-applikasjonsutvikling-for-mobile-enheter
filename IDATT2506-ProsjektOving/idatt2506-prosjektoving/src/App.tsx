import React, { useState, useEffect } from 'react';
import { IonApp} from "@ionic/react";
import { TodoList } from "./models/types"
import ListTabs from './components/ListTabs';
import TodoInput from './components/TodoInput';
import TodoListView from './components/TodoListView';
import { readLists, saveLists } from './utils/fileStorage';

const App: React.FC = () => {
    const [lists, setLists] = useState<TodoList[]>([]);
    const [activeListId, setActiveListId] = useState<number>(0);
}

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
