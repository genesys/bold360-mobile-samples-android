//
//  RestoreChat.swift
//  HyattDemo
//
//  Created by Nissim Pardo on 21/04/2019.
//  Copyright © 2019 Nissim Pardo. All rights reserved.
//

import UIKit
import Bold360AI
import CoreData

enum RestoreChatState {
    case prsented
    case dismissed
    case pending
}

protocol RestoreChatDelegate {
    func didReceiveBackgroundMessage(_ message: StorableChatElement)
}

class RestoreChat: HistoryProvider {
    
    var managedContext: NSManagedObjectContext!
    var state = RestoreChatState.pending
    var delegate: RestoreChatDelegate?
    
    init(_ context: NSManagedObjectContext) {
        self.managedContext = context
    }
    
    func fetch(_ from: Int, handler: (([Any]?) -> Void)!) {
        let fetchRequest = NSFetchRequest<ChatHistoryItem>(entityName: "ChatHistoryItem")
        fetchRequest.sortDescriptors = [NSSortDescriptor(key: "timeStamp", ascending: true)]
        do {
            let result = try self.managedContext.fetch(fetchRequest)
            handler(result)
        } catch {
            print("fetch failed")
        }
    }
    
    func store(_ item: StorableChatElement!) {
        let _item = NSEntityDescription.insertNewObject(forEntityName: "ChatHistoryItem", into: self.managedContext) as! ChatHistoryItem
        _item.element = item
        do {
            try self.managedContext.save()
        } catch let error as NSError {
            print("Could not save. \(error)")
        }
        if self.state == .dismissed {
            self.delegate?.didReceiveBackgroundMessage(item)
        }
    }
    
    func remove(_ timestampId: TimeInterval) {
        
    }
    
    func update(_ timestampId: TimeInterval, newTimestamp: TimeInterval, status: StatementStatus) {
        let fetchRequest = NSFetchRequest<ChatHistoryItem>(entityName: "ChatHistoryItem")
        let id = Int64(timestampId)
        fetchRequest.predicate = NSPredicate(format: "itemId == %@", NSNumber(value: id))
        do {
            let result = try self.managedContext.fetch(fetchRequest)
            if let item = result.first {
                item.itemStatus = Int16(status.rawValue)
                store(item.element)
                self.managedContext.delete(result.first!)
            }
        } catch {
            print("fetch failed")
        }
    }
    

}
