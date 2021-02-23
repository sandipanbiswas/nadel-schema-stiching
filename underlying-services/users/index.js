const { ApolloServer, gql } = require('apollo-server');

const typeDefs = gql`
  type Query {
      user(id:ID!): User
  }
  type User {
      id: ID!
      name: String
  }
`;

const users = [
  {
    id: 'user-1',
    name: 'Andi',
  },
  {
    id: 'user-2',
    name: 'Brad',
  },
];

const resolvers = {
  Query: {
    user: (obj, { id }, context, info) => {
      return users.find(user => user.id == id);
    },
  },
};


const server = new ApolloServer({ typeDefs, resolvers });

server.listen().then(({ url }) => {
  console.log(`🚀  Server ready at ${url}`);
});