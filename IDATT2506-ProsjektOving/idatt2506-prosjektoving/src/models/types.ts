
//Represents one element in the list
export interface ToDoItem {
    id: string;
    text: string;
    done: boolean;
    }

    //Represents a collection  of elements with a name for the list
    export interface ToDoIList {
    name: string;
    items: ToDoItem[];
    }