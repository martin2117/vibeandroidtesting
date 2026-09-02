import React from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { PRODUCTS } from '../data/products';
import { useCart } from '../context/CartContext';

export default function CatalogScreen() {
  const { addToCart } = useCart();

  return (
    <View style={styles.container}>
      <FlatList
        testID="catalog-list"
        data={PRODUCTS}
        keyExtractor={(item) => item.id}
        contentContainerStyle={{ padding: 16 }}
        renderItem={({ item }) => (
          <View testID={`product-${item.id}`} style={styles.card}>
            <View style={styles.thumb}><Text style={styles.thumbIcon}>{item.icon}</Text></View>
            <View style={styles.info}>
              {/* FIXED (BUG-007): clamp to one line with an ellipsis. */}
              <Text
                testID={`name-${item.id}`}
                style={styles.name}
                numberOfLines={1}
                ellipsizeMode="tail"
              >
                {item.name}
              </Text>
              <Text style={styles.price}>${item.price}</Text>

              {!item.inStock && (
                // FIXED (BUG-008): the "Out of Stock" badge is red.
                <Text testID={`stock-${item.id}`} style={styles.badge}>
                  Out of Stock
                </Text>
              )}
            </View>

            <TouchableOpacity
              testID={`add-${item.id}`}
              disabled={!item.inStock}
              style={[styles.addBtn, !item.inStock && styles.addBtnDisabled]}
              onPress={() => addToCart(item)}
            >
              <Text style={styles.addBtnText}>Add</Text>
            </TouchableOpacity>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f6f6f9' },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 14,
    marginBottom: 12,
  },
  thumb: { width: 52, height: 52, borderRadius: 8, backgroundColor: '#e6e4f5', marginRight: 14, alignItems: 'center', justifyContent: 'center' },
  thumbIcon: { fontSize: 28 },
  info: { flex: 1 },
  name: { fontSize: 16, fontWeight: '600', color: '#1a1442' },
  price: { fontSize: 15, color: '#3b2fb5', marginTop: 2 },
  // FIXED (BUG-008): red badge.
  badge: { marginTop: 4, color: '#c0392b', fontWeight: '700', fontSize: 12 },
  addBtn: { backgroundColor: '#3b2fb5', paddingVertical: 10, paddingHorizontal: 16, borderRadius: 8 },
  addBtnDisabled: { backgroundColor: '#bbb' },
  addBtnText: { color: '#fff', fontWeight: '700' },
});
