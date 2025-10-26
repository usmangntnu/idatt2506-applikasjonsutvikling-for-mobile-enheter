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