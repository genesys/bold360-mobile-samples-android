//
//  ChatHistoryItem+CoreDataClass.swift
//  BasicSample
//
//  Created by Nissim Pardo on 29/04/2019.
//  Copyright © 2019 bold360ai. All rights reserved.
//
//

import Foundation
import CoreData
import Bold360AI


public class ChatHistoryItem: NSManagedObject, StorableChatElement {
    
    public var storageKey: String! {
        get {
            return self.json
        }
    }
    
    public var statementScope: StatementScope {
        set {
            self.scope = Int16(newValue.rawValue)
        }
        get {
            return StatementScope(rawValue: Int(self.scope)) ?? StatementScope.None
        }
    }
    
    public var status: StatementStatus {
        set {
            self.itemStatus = Int16(newValue.rawValue)
        }
        get {
            return StatementStatus(rawValue: Int(self.itemStatus)) ?? StatementStatus.Pending
        }
    }
    
    public var elementId: NSNumber! {
        return self.itemId as NSNumber
    }
    
    public var type: ChatElementType {
        return ChatElementType(rawValue: Int(self.itemType)) ?? ChatElementType.OutgoingElement
    }
    
    public var timestamp: Date! {
        return self.timeStamp as Date?
    }
    
    public var text: String! {
        return self.itemText
    }
    
    public var source: ChatElementSource {
        set {
            self.itemSource = Int16(newValue.rawValue)
        }
        get {
            return ChatElementSource(rawValue: Int(self.itemSource)) ?? ChatElementSource.history
        }
    }
    
    public var configuration: ChatElementConfiguration!
    
    public var removable: Bool {
        set {
            self.isRemovable = newValue
        }
        get {
            return self.isRemovable
        }
    }
    
    
    
    
    
    public var element: StorableChatElement! {
        didSet {
            self.itemId = self.element.elementId.int64Value
            self.itemType = Int16(self.element.type.rawValue)
            
            self.timeStamp = self.element.timestamp as NSDate
            self.itemText = self.element.text
            self.itemSource = Int16(self.element.source.rawValue)
            self.isRemovable = self.element.removable
            if let context = self.managedObjectContext {
                self.config = NSEntityDescription.insertNewObject(forEntityName: "ChatConfiguration", into: context) as? ChatConfiguration
                self.config?.configuration = self.element.configuration
            }
            self.itemStatus = Int16(self.element.status.rawValue)
            self.scope = Int16(self.element.statementScope.rawValue)
            self.json = self.element.storageKey
        }
    }
    
    
    
    
    
}
