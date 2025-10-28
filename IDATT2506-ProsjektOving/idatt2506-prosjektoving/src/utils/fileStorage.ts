import {Filesystem, Directory, Encoding} from '@capacitor/filesystem';
import {ToDoList} from "../models/types";

//KRAV 9: Name of file for saving and retrieving data
const FILE_NAME = 'lists.json';

//krav 9: Saves the provided lists to a file in JSON format
export const saveLists = async (lists: ToDoList[]) => {
    try {
        console.log('📝 Attempting to save lists:', JSON.stringify(lists, null, 2));
        const result = await Filesystem.writeFile({
            path: FILE_NAME,
            data: JSON.stringify(lists),
            directory: Directory.Documents,
            encoding: Encoding.UTF8
        });
        console.log('✅ Save result:', result);
    } catch (error) {
        console.error('❌ Error saving lists:', error);
    }
};

//krav 9: Function that retrieves the saved lists from the file
export const readLists = async (): Promise<ToDoList[]> => {
    try {
        console.log('📖 Attempting to read from:', FILE_NAME);
        const contents = await Filesystem.readFile({
            path: FILE_NAME,
            directory: Directory.Documents,
            encoding: Encoding.UTF8
        });
        console.log('📄 Raw file contents:', contents);
        console.log('📄 File data:', contents.data);
        const parsed = JSON.parse(contents.data as string);
        console.log('✅ Parsed lists:', JSON.stringify(parsed, null, 2));
        return parsed || [];
    } catch (error) {
        console.error('⚠️ Error reading lists:', error);
        return [];
    }
};


