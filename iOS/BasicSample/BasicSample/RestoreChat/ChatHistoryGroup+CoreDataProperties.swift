//
//  ChatHistoryGroup+CoreDataProperties.swift
//  BasicSample
//
//  Created by Nissim Pardo on 30/04/2019.
//  Copyright © 2019 bold360ai. All rights reserved.
//
//

import Foundation
import CoreData


extension ChatHistoryGroup {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<ChatHistoryGroup> {
        return NSFetchRequest<ChatHistoryGroup>(entityName: "ChatHistoryGroup")
    }

    @NSManaged public var groupId: String?
    @NSManaged public var items: NSSet?

}

// MARK: Generated accessors for items
extension ChatHistoryGroup {

    @objc(addItemsObject:)
    @NSManaged public func addToItems(_ value: ChatHistoryItem)

    @objc(removeItemsObject:)
    @NSManaged public func removeFromItems(_ value: ChatHistoryItem)

    @objc(addItems:)
    @NSManaged public func addToItems(_ values: NSSet)

    @objc(removeItems:)
    @NSManaged public func removeFromItems(_ values: NSSet)

}
