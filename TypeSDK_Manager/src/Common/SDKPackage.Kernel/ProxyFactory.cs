namespace SDKPackage.Kernel
{
    using System;
    using System.Collections.Generic;
    using System.Reflection;
    using System.Reflection.Emit;
    using System.Runtime.CompilerServices;
    /// <summary>
    /// 提供代理实现工厂
    /// </summary>
    public sealed class ProxyFactory
    {
        private static Dictionary<string, CreateInstanceHandler> m_Handlers = new Dictionary<string, CreateInstanceHandler>();

        private ProxyFactory()
        {
        }
        /// <summary>
        /// 创建带参数的对象实例
        /// </summary>
        /// <param name="objtype"></param>
        /// <param name="key"></param>
        /// <param name="ptypes"></param>
        private static void CreateHandler(Type objtype, string key, Type[] ptypes)
        {
            lock (typeof(ProxyFactory))
            {
                if (!m_Handlers.ContainsKey(key))
                {
                    DynamicMethod method = new DynamicMethod(key, typeof(object), new Type[] { typeof(object[]) }, typeof(ProxyFactory).Module);
                    ILGenerator iLGenerator = method.GetILGenerator();
                    ConstructorInfo constructor = objtype.GetConstructor(ptypes);
                    iLGenerator.Emit(OpCodes.Nop);
                    for (int i = 0; i < ptypes.Length; i++)
                    {
                        iLGenerator.Emit(OpCodes.Ldarg_0);
                        iLGenerator.Emit(OpCodes.Ldc_I4, i);
                        iLGenerator.Emit(OpCodes.Ldelem_Ref);
                        if (ptypes[i].IsValueType)
                        {
                            iLGenerator.Emit(OpCodes.Unbox_Any, ptypes[i]);
                        }
                        else
                        {
                            iLGenerator.Emit(OpCodes.Castclass, ptypes[i]);
                        }
                    }
                    iLGenerator.Emit(OpCodes.Newobj, constructor);
                    iLGenerator.Emit(OpCodes.Ret);
                    CreateInstanceHandler handler = (CreateInstanceHandler) method.CreateDelegate(typeof(CreateInstanceHandler));
                    m_Handlers.Add(key, handler);
                }
            }
        }

        public static T CreateInstance<T>()
        {
            return CreateInstance<T>(null);
        }
        /// <summary>
        /// 创建无参的对象实例
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="parameters"></param>
        /// <returns></returns>
        public static T CreateInstance<T>(params object[] parameters)
        {
            Type objtype = typeof(T);
            Type[] parameterTypes = GetParameterTypes(parameters);
            string key = typeof(T).FullName + "_" + GetKey(parameterTypes);
            if (!m_Handlers.ContainsKey(key))
            {
                CreateHandler(objtype, key, parameterTypes);
            }
            return (T) m_Handlers[key](parameters);
        }

        private static string GetKey(params Type[] types)
        {
            if ((types == null) || (types.Length == 0))
            {
                return "null";
            }
            return string.Concat((object[]) types);
        }
        /// <summary>
        /// 获取参数类型
        /// </summary>
        /// <param name="parameters"></param>
        /// <returns></returns>
        private static Type[] GetParameterTypes(params object[] parameters)
        {
            if (parameters == null)
            {
                return new Type[0];
            }
            Type[] typeArray = new Type[parameters.Length];
            for (int i = 0; i < parameters.Length; i++)
            {
                typeArray[i] = parameters[i].GetType();
            }
            return typeArray;
        }
        /// <summary>
        /// 创建对象实例
        /// </summary>
        /// <param name="parameters"></param>
        /// <returns></returns>
        public delegate object CreateInstanceHandler(object[] parameters);
    }
}

