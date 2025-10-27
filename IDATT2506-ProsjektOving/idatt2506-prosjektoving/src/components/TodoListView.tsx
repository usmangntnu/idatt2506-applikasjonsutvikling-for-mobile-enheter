import React from 'react';
import { IonList, IonItem, IonLabel } from '@ionic/react';
import { ToDoItem } from '../models/types';

//Props received from App.tsx
interface Props {
    items: ToDoItem[];
    toggleDone: (id: string) => void;
}

//Component itself and shows the list of elements
const TodoListView: React.FC<Props> = ({ items, toggleDone }) => {
    return (
        <IonList>
            {/* Maps through all items in the active list */}
            {items.map(item => (
                // Each item is clickable to toggle its done status
                <IonItem key={item.id} button onClick={() => toggleDone(item.id)}>
                    {/* Strike-through text if item is marked as done */}
                    <IonLabel style={{ textDecoration: item.done ? 'line-through' : 'none' }}>
                        {item.text}
                    </IonLabel>
                </IonItem>
            ))}
        </IonList>
    );
};

export default TodoListView;
